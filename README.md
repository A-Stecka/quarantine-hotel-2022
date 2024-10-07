# Hotel Quarantine Guest Door Opening System Using RFID Technology  
A project for a quarantine hotel application created as part of the course: Basics of the Internet of Things
-
The aim of the project is to create a system that allows guests of the quarantine hotel to automatically open the doors to their rooms and the main gate of the hotel using RFID cards instead of keys.

Guests check into the hotel via a mobile application to minimize contact with reception staff. After checking in, each guest receives an RFID card instead of a key to their room. This card is assigned to the guest for the duration of their stay at the hotel and allows them to open the door to their room. The staff provides the guest with a token to enter into the application; this token links the guest to the room they will be staying in and the RFID card they will use to open their room door. Each token is valid for up to 24 hours from creation and is one-time use—once used by one guest, it becomes invalid.

During checkout from the hotel, the guest is required to return the RFID card, which can then be used by other guests. Failure to return the RFID card incurs a deposit fee of 50 PLN. If the card is lost, the guest is obliged to block the card to prevent unauthorized use.

If a guest still in quarantine uses their card to open the main hotel gate, they will not be allowed to exit. Only guests whose quarantine has ended will be allowed through the gate. Behind the gate is the reception, where the guest checks out of the hotel and hands over their card to the staff.

As part of the project, a web and mobile application was designed using a database. Containerization (Docker) with docker-compose orchestration was used to set up the services. Four services were established: the database, application server, MQTT broker, and web server. The designed mobile application was implemented as a native mobile application for the Android system, and the designed web application was implemented using HTML and JavaScript.

The main branch contains the project documentation. The implementation of the mobile application can be found in the mobile_app branch. The implementation of the main application node (database, application server, MQTT broker, and web server services) can be found in the management_node branch.
-
This project was completed in collaboration with [P. Walkowiak](https://github.com/PawelWal), [B. Tlołka](https://github.com/Boguslawa-Tlolka), and [J. Wdziękońska](https://github.com/JoannaWdziekonska).
