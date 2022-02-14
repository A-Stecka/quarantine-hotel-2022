import mariadb
import random


class Mgmt:
    def __init__(self):
        self._conn = mariadb.connect(
            user="root",
            password="p@ss_123",
            host="database",
            database="hotel")

    def check_in(self, guest_id: int, token: int):
        if self.check_if_token_exists(token) and guest_id <= self.__get_last_guest_id():
            check_ins_numb = self.get_one_parameter_list("SELECT COUNT(id) FROM CheckIns")[0]
            cmd = "SELECT FK_room, FK_rfid FROM Tokens WHERE id={0} and status=1".format(token)
            cur = self._conn.cursor()
            cur.execute(cmd)
            room_id, rfid_id = None, None
            for room, rfid in cur:
                room_id = room
                rfid_id = rfid
            cur.close()
            if room_id and rfid_id:
                cur = self._conn.cursor()
                cmd2 = "INSERT INTO CheckIns (FK_room, FK_guest, FK_rfid, " \
                       "validSince, validUntil) Values({0}, {1}, {2},NOW(),NULL)".format(room_id, guest_id, rfid_id)
                cur.execute(cmd2)
                cur.close()
                cur = self._conn.cursor()
                cmd3 = "UPDATE TOKENS SET status=0 WHERE id={0}".format(token);
                cur.execute(cmd3)
                cur.close()
                if check_ins_numb + 1 == self.get_one_parameter_list("SELECT COUNT(id) FROM CheckIns")[0]:
                    self._conn.commit()
                    return {"result": "success", "room": room_id}
        return {"result": "fail", "room": -1}

    def check_out(self, guest_id: int):
        if guest_id <= self.__get_last_guest_id():
            cmd = "UPDATE CheckIns SET validUntil=SYSDATE() WHERE FK_guest={0} AND validUntil is NULL".format(guest_id)
            cur = self._conn.cursor()
            cur.execute(cmd)
            cur.close()
            cmd2 = "SELECT FK_guest FROM CheckIns WHERE FK_guest={0} AND validUntil is NULL".format(guest_id)
            if not self.get_one_parameter_list(cmd2):
                self._conn.commit()
                return {"result": "success"}
        return {"result": "fail"}

    def block_rfid_card(self, guest_id: int):
        rfid = self.get_guest_card(guest_id)
        if rfid:
            rfid = rfid["rfid"]
            cmd = "UPDATE RFIDs SET status=0 WHERE RFID_no='{0}'".format(rfid)
            cur = self._conn.cursor()
            cur.execute(cmd)
            cur.close
            # validate
            cmd = "SELECT status FROM RFIDs WHERE RFID_no='{0}'".format(rfid)
            if 0 in self.get_one_parameter_list(cmd):
                self._conn.commit()
                return {"result": "blocked"}
        return {"result": "fail"}


    def get_countries(self):
        cur = self._conn.cursor()
        countries = {}
        cur.execute("SELECT * FROM Countries")
        for country_id, name in cur:
            countries[country_id] = name
        cur.close()
        return countries

    def add_guest(self, name: str, surname: str, doc_no: str, phone: int, email: str,
                  address: str, zip_code: str, city: str, country_code: int):
        if country_code in self.get_countries().keys():
            guest_id = self.__get_last_guest_id() + 1
            cur = self._conn.cursor()
            cmd = "INSERT INTO Guests VALUES({0}, '{1}', '{2}', '{3}', '{4}', '{5}', '{6}', '{7}', '{8}', {9})".format(
                guest_id, name, surname, doc_no, phone, email, address, zip_code, city, country_code)
            cur.execute(cmd)
            cur.close()
            if guest_id == self.__get_last_guest_id():
                self._conn.commit()
                return {"guest_id": guest_id}
        return {"guest_id": -1}

    def get_guest(self, guest_id: int):
        result = {}
        if guest_id in self.get_one_parameter_list("SELECT id FROM Guests"):
            cmd = "SELECT Guests.name, surname, document_no, phone_no, email, address, zip_code, city, Countries.name  " \
                  "FROM Guests JOIN Countries ON Guests.FK_country = Countries.id WHERE Guests.id={0}".format(guest_id)
            cur = self._conn.cursor()
            cur.execute(cmd)
            for name, sur, doc, pho, ema, add, code, cit, country in cur:
                result = {"name": name, "surname": sur, "document_no": doc,
                          "phone_no": pho, "email": ema, "address": add,
                          "zip_code": code, "city": cit, "country": country}
            cur.close()
        return result


    def __get_last_guest_id(self):
        return self.get_one_parameter_list("SELECT COUNT(id) FROM Guests")[0]

    def get_guest_card(self, guest_id: int):
        cmd = "SELECT RFID_no FROM RFIDs JOIN CheckIns ON RFIDs.id = CheckIns.FK_rfid WHERE CheckIns.FK_guest = {0} AND (validUntil >= NOW() OR validUntil IS NULL)".format(guest_id)
        numbers = self.get_one_parameter_list(cmd)
        if numbers:
            return {"rfid": numbers[0]}
        else:
            return {}

    def check_rfid(self, rfid_nbr: int):
        cur = self._conn.cursor()
        cmd = "SELECT name, surname, IF(validUntil <= NOW(), 1, 0) FROM Guests RIGHT JOIN CheckIns ON Guests.id = CheckIns.FK_guest RIGHT JOIN RFIDs ON RFIDs.id = CheckIns.FK_rfid WHERE status=1 AND RFIDs.RFID_no={0}".format(rfid_nbr)
        cur.execute(cmd)
        msg = []
        for name, surname, quarantineEnded in cur:
            if not msg:
                if quarantineEnded == 1:
                    msg = [name +" "+ surname, "open"]
                else:
                    msg = [name +" "+ surname, "alarm"]
        cur.close()
        return msg

    def find_guests(self, surname: str):
        cur = self._conn.cursor()
        cmd = "SELECT DISTINCT Guests.id, name, surname, RFID_no, phone_no, email FROM Guests LEFT JOIN CheckIns ON Guests.id = CheckIns.FK_guest LEFT JOIN RFIDs ON RFIDs.id = CheckIns.FK_rfid WHERE surname='{0}'".format(surname)
        cur.execute(cmd)
        result = []
        for guest_id, name, surname, RFID_no, phone_no, email in cur:
            result.append({"id": guest_id, "name": name, "surname": surname, "RFID": RFID_no, "phone": phone_no, "email": email})
        cur.close()
        return result

    def get_rfids(self):
        cur = self._conn.cursor()
        cur.execute("SELECT id, RFID_no FROM RFIDs WHERE status=1")
        cards = []
        for card_id, nbr in cur:
            cards.append({"id": card_id, "RFID_no": nbr})
        return cards

    def get_free_rooms(self):
        cur = self._conn.cursor()
        cur.execute("SELECT DISTINCT number FROM Rooms LEFT JOIN CheckIns ON Rooms.number = CheckIns.FK_room WHERE CheckIns.validUntil <= CURDATE() OR id is NULL")
        rooms = []
        for nbr in cur:
            rooms.append(nbr[0])
        return rooms

    def __check_rfid(self, rfid_id: str):
        for pair in self.get_rfids():
            if pair["id"] == rfid_id:
                return True
        return False

    def add_rfid_to_room(self, rfid_id: str, room_id: str):
        cur = self._conn.cursor()
        token = random.randint(10000, 100000)
        if room_id in self.get_free_rooms() and self.__check_rfid(rfid_id):
            cont = True
            while cont:
                if self.check_if_token_exists(token):
                    token = random.randint(10000, 100000)
                else:
                    cont = False
            cmd = "INSERT INTO Tokens VALUES({0}, {1}, {2}, {3}, CURDATE())".format(token, room_id, rfid_id, 1)
            cur.execute(cmd)
            cur.close()
            if self.check_if_token_exists(token):
                self._conn.commit()
                return {"token": token}

    def check_if_token_exists(self, token_nbr: int):
        cur = self._conn.cursor()
        cmd = "SELECT id FROM Tokens WHERE id={0}".format(str(token_nbr))
        cur.execute(cmd)
        output = None
        for nbr in cur:
            output = nbr[0]
        cur.close()
        return output

    def get_room_info(self, room_id: int):
        cur = self._conn.cursor()
        cmd = "SELECT Guests.id, name, surname, RFID_no, phone_no, email FROM Rooms JOIN CheckIns ON Rooms.number=CheckIns.FK_room JOIN RFIDs ON CheckIns.FK_rfid = RFIDs.id JOIN Guests ON CheckIns.FK_guest = Guests.id WHERE Rooms.number={0} AND (validUntil IS NULL OR validUntil >= CURDATE())".format(room_id)
        cur.execute(cmd)
        guest = []
        for g_id, name, surname, rfid, phone, email in cur:
            guest.append({"id": g_id, "name": name, "surname": surname, "RFID": rfid, "phone": phone, "email": email})
        cur.close()
        return guest

    def get_one_parameter_list(self, command: str):
        cur = self._conn.cursor()
        cur.execute(command)
        some_list = []
        for name in cur:
            some_list.append(name[0])
        cur.close()
        return some_list
