from typing import Optional

from fastapi import FastAPI

from fastapi.middleware.cors import CORSMiddleware

from pydantic import BaseModel, SecretStr

from app.mgmt import Mgmt
import app.mqtt_conn as mqtt

app = FastAPI(root_path="/api")
mn = Mgmt()
mqtt.connect_to_broker("hotel.domain")

origins = ['*']
app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class FindGuest(BaseModel):
    surname: SecretStr

class RfidToRoom(BaseModel):
    room_id: int
    rfid_id: int

class Room(BaseModel):
    room_id: int

class Guest(BaseModel):
    guest_id: int

class NewGuest(BaseModel):
    email: str
    name: str
    surname: str
    doc_no: str
    phone_no: int
    address: str
    zip_code: str
    city: str
    country_code: int

class CheckIn(BaseModel):
    token: int
    guest_id: int

class CheckOut(BaseModel):
    guest_id: int

@app.post("/countries")
def get_countries():
    return mn.get_countries()

@app.post("/findGuest")
def find_guests(findGuest: FindGuest):
    return mn.find_guests(findGuest.surname.get_secret_value())

@app.post("/addRifdToRoom")
def add_rfid_to_room(rdifToRoom: RfidToRoom):
    return mn.add_rfid_to_room(rdifToRoom.rfid_id, rdifToRoom.room_id)

@app.post("/getRfids")
def get_rfids():
    return mn.get_rfids()

@app.post("/getRooms")
def get_free_rooms():
    return mn.get_free_rooms()

@app.post("/getRoomInfo")
def get_room_info(room: Room):
    return mn.get_room_info(room.room_id)

@app.post("/getGuestRfid")
def get_guest_rfid(guest: Guest):
    return mn.get_guest_card(guest.guest_id)

@app.post("/blockCard")
def block_rfid_card(guest: Guest):
    return mn.block_rfid_card(guest.guest_id)

@app.post("/addGuest")
def add_guest(guest: NewGuest):
    return mn.add_guest(guest.name, guest.surname, guest.doc_no, guest.phone_no, guest.email,
                  guest.address, guest.zip_code, guest.city, guest.country_code)

@app.post("/getGuest")
def get_guest(guest: Guest):
    return mn.get_guest(guest.guest_id)

@app.post("/checkIN")
def check_in(checkIn: CheckIn):
    return mn.check_in(checkIn.guest_id, checkIn.token)

@app.post("/checkOut")
def check_out(guest: CheckOut):
    return mn.check_out(guest.guest_id)