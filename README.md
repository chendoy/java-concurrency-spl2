# SPL_ass2

Order Receipt:
seller = the thread (the sellingService microService that handled the selling)
customer = the thread (the webApiService microService that send message to the sellingService microService to buy a spesific book)

issuedTick = the tick 1 line before this function returns (it is the tick when the receipt issued)
orderTick= the tick in which the WEBAPI Service fetch the (PuarchesBook book1 event) from his queue
proccessTick= the tick in which  SellingService fetchs from his Message's queue the WebApi event (sellBook book1 event)


לוודא שאין synchronised איפה שיש atomic reference

בתוך הcatch של interruptedExeption לעשות קריאה לinterrupt()

https://www.cs.bgu.ac.il/~spl191/phpBB/viewtopic.php?f=4&t=402

