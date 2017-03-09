#include "main.h"

int send_array(int socket, void *buffer, size_t length)
{
    char *ptr = (char*) buffer;
    while (length > 0)
    {
        int i = send(socket, ptr, length, 0);
        if (i < 1) return 0;
        ptr += i;
        length -= i;
    }
    return 1;
}

unsigned calculate_CRC(unsigned char* data, size_t length)
{
    int sum = 0;
    for (int i = 0; i < length; i++)
        sum += data[i];
    return sum & 0xFF;
}

void printHexArray(unsigned char* buf, size_t len) {
    printf("\n");
    int i;
    for ( i = 0; i < len; i++) {
        printf("0x%02x ", buf[i]);
    }
    printf("\n");
}

void sineModifyBuf(unsigned char* sendBuf, size_t start, size_t len)
{
    static int counter = 0;
    int val = 0;
    counter += 5;
    if (counter > 180) {
        counter = 0;
    }
    val = sin(counter * PI/180) * 200;
    if (val < 0)
        val = val * (-1);

    for (int i=start; i < len; i++)
        sendBuf[i] = val;
}

unsigned char* createBingoResponse(int packetType, size_t *len)
{
  unsigned char* respPattern;
  switch(packetType)
  {
    case 1:
      *len = RESPONSE_1_SIZE;
      respPattern = responsePacket1;
      break;
    case 2:
      *len = RESPONSE_2_SIZE;
      respPattern = malloc(sizeof(unsigned char) * *len);
      memcpy(respPattern, responsePacket2, *len);
      sineModifyBuf(respPattern, 2, *len - 1);
      break;
    case 3:
      *len = RESPONSE_3_SIZE;
      respPattern = responsePacket3;
      break;
    case 4:
      *len = RESPONSE_4_SIZE;
      respPattern = responsePacket4;
      break;
    case 5:
      *len = RESPONSE_5_SIZE;
      respPattern = responsePacket5;
      break;
  }
  unsigned char* sendBuf = malloc(sizeof(unsigned char) * *len);
  memcpy(sendBuf, respPattern, *len);
  unsigned crc = calculate_CRC(sendBuf, *len);
  sendBuf[*len - 1] = crc;
  return sendBuf;
}

void printAndSendResponse(int sockId, unsigned char* sendBuf, size_t len)
{
    printf("<-Send ans - %ld bytes, CRC: 0x%02x ", len, sendBuf[len - 1]);
    printHexArray(sendBuf, len);
    send_array(sockId, sendBuf, len);
}

void ctrlCHandler(int signum)
{
    printf("ControlC Handler\n");
    stop = 1;
    close(clientSockId);
    close(servSockId);
    exit(0);
}

int main()
{
    struct sockaddr_rc loc_addr = {0}, rem_addr = {0} ;
    unsigned char recvBuf[5] = {0} ;
    char addrBuf[64] = {0};
    int bytes_read ;
    unsigned int opt = sizeof(rem_addr);

    signal(SIGINT, ctrlCHandler);
    servSockId = socket(AF_BLUETOOTH, SOCK_STREAM, BTPROTO_RFCOMM);
    loc_addr.rc_family = AF_BLUETOOTH;
    loc_addr.rc_channel = 1;

    bind(servSockId, (struct sockaddr*)&loc_addr, sizeof(loc_addr));
    listen(servSockId,1);

    printf("Waiting for clients...\n");
    while(!stop) {
        clientSockId = accept(servSockId, (struct sockaddr *)&rem_addr, &opt );

        ba2str (&rem_addr.rc_bdaddr, addrBuf );
        fprintf(stderr, "New conn: %s\n", addrBuf);
        memset (addrBuf, 0, sizeof(addrBuf));
        size_t sendBufLen;
        clientRunning = 1;
        while(clientRunning && !stop) {
            bytes_read = recv(clientSockId, recvBuf, sizeof(recvBuf), 0);
            if (bytes_read > 0)
            {
                printf("->Recv query - %d bytes: ", bytes_read);
                printHexArray(recvBuf, bytes_read);
                unsigned char* sendBuf;

                if (!memcmp(recvBuf, requestPacket1, ASK_FRAME_SIZE)) {
                    printf("Found Request Packet 1\n");
                    sendBuf = createBingoResponse(1, &sendBufLen);
                } else if (!memcmp(recvBuf, requestPacket2, ASK_FRAME_SIZE)) {
                  printf("Found Request Packet 2\n");
                    sendBuf = createBingoResponse(2, &sendBufLen);
                } else if (!memcmp(recvBuf, requestPacket3, ASK_FRAME_SIZE)) {
                    printf("Found Request Packet 3\n");
                    sendBuf = createBingoResponse(3, &sendBufLen);
                } else if (!memcmp(recvBuf, requestPacket4, ASK_FRAME_SIZE)) {
                    printf("Found Request Packet 4\n");
                    sendBuf = createBingoResponse(4, &sendBufLen);
                } else if (!memcmp(recvBuf, requestPacket5, ASK_FRAME_SIZE)) {
                    printf("Found Request Packet 5\n");
                    sendBuf = createBingoResponse(5, &sendBufLen);
                } else {
                  printf("Found setting packet\n ");
                  send(clientSockId, recvBuf, 1, 0);
                  send(clientSockId, recvBuf, 1, 0);
                  send(clientSockId, recvBuf, 1, 0);
                  continue;
                }
                printAndSendResponse(clientSockId, sendBuf, sendBufLen);
                free(sendBuf);
                usleep(SLEEP_AFTER_SENT);
            }
            else {
                printf("Client not running!");
                clientRunning = 0;
            }
        }
    }
    printf("\nClosing...\n");
    return 0 ;
}
