# System otwierania drzwi przez gości hotelu kwarantannowego wykorzystujący technologię RFID
Projekt aplikacji dla hotelu kwarantannowego wykonany w ramach przedmiotu Podstawy Internetu Rzeczy
-
Celem projektu jest stworzenie systemu umożliwiającego umożliwiającego gościom hotelu kwarantannowego automatyczne otwieranie drzwi do ich pokojów oraz głównej bramy hotelu za pomocą kart RFID zamiast kluczy.

Gość melduje się w hotelu za pośrednictwem aplikacji mobilnej, aby ograniczyć kontakt z osobami na recepcji. Po zameldowaniu się w hotelu każdy gość zamiast klucza do swojego pokoju otrzymuje kartę RFID. Ta karta jest przypisana do tego gościa przez cały jego pobyt w hotelu i umożliwia mu otwarcie drzwi do jego pokoju. Pracownik przekazuje gościowi token do wpisania do aplikacji – token służy do powiązania gościa z pokojem, w którym będzie się zatrzymywał, i kartą RFID, której będzie używał do otwarcia drzwi do swojego pokoju. Każdy token jest ważny przez co najwyżej 24h od utworzenia oraz jest jednorazowy – po wykorzystaniu przez jednego gościa przestaje być ważny.

Podczas wymeldowywania się z hotelu gość jest zobowiązany zdać kartę RFID, która następnie może być wykorzystana przez innych gości. Nie zdanie karty RFID jest związane z koniecznością opłacenia kaucji w wysokości 50zł. W przypadku zgubienia karty, gość jest zobowiązany zablokować kartę, żeby nie mogła zostać ona wykorzystana przez osoby nieuprawnione.

Jeżeli gość, który dalej jest na kwarantannie, użyje swojej karty aby otworzyć główną bramę hotelu, nie zostanie on wypuszczony. Jedynie goście, których kwarantanna się skończyła, zostaną przepuszczeni przez bramkę. Za bramką znajduje się recepcja, przy której gość wymeldowuje się z hotelu i zdaje swoją kartę pracownikowi.

W ramach projektu zaprojektowano aplikacje webową i mobilną korzystające z bazy danych. Do postawienia serwisów wykorzystano konteneryzację (Docker) z narzędziem orkiestryzacji docker-compose. Postawiono cztery serwisy – bazę danych, serwer aplikacji, broker MQTT oraz serwer webowy. Zaprojektowaną aplikację mobilną zaimplementowano jako natywną aplikację mobilną na system Android, a zaprojektowaną aplikację webową zaimplementowano korzystając z języków HTML i JavaScript.
-
Branch main zawiera dokumentację projektu. Implementacja aplikacji mobilnej znajduje się w branchu mobile_app. Implementacja głównego węzła aplikacji (serwisy bazy danych, serwera aplikacji, brokera MQTT oraz serwera webowy) znajduje się w branchu management_node.
-
Projekt wykonany wspólnie z [P. Walkowiakiem](https://github.com/PawelWal), [B. Tlołką](https://github.com/Boguslawa-Tlolka) oraz [J. Wdziękońską](https://github.com/JoannaWdziekonska).
