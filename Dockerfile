FROM coturn/coturn:4.6-alpine

# Открываем нужные порты
EXPOSE 3478 3478/udp 5349 5349/udp
