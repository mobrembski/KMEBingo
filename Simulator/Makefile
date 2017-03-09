CC = gcc
CFLAGS = -g -std=gnu99 -Wall
LIBS = -lbluetooth -lm
OBJS = src/main.c

all:	BingoRFComm

BingoRFComm: $(OBJS)
	$(CC) -o $@ $^ $(CFLAGS) $(LIBS)

test:	clean all
	valgrind ./BingoRFComm

clean:
	rm BingoRFComm

.PHONY: all
