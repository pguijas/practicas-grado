namespace java es.udc.ws.ficrun.thrift

struct ThriftRunDto {
    1: i64 runId;
    2: string name;
    3: string city;
    4: string startDate;
    5: string description;
    6: double price;
    7: i32 maxRunners
    8: i32 numInscriptions
}

//No me interesa para mi parte, no necesito intercambiar información de inscriptions (Pedro)
//struct ThriftInscriptioneDto {}

exception ThriftInputValidationException {
    1: string message
}

exception ThriftInstanceNotFoundException {
    1: string instanceId
    2: string instanceType
}

exception ThriftDorsalPickedException {
    1: i64 inscriptionId;
}

exception ThriftWrongCreditCardException {
    1: i64 inscriptionId;
    3: string creditCardNumber;
}


service ThriftRunService {

   //Func-1 Sergio
   i64 addRun(1: ThriftRunDto runDto) throws (1: ThriftInputValidationException e)

   //Func-3 Sergio
   list<ThriftRunDto> findRuns(1: string beforeDate, 2: string city) throws (1: ThriftInputValidationException e)

   //Func-2 Pedro
   ThriftRunDto findRun(1: i64 runId) throws (1: ThriftInstanceNotFoundException e)

   //Func-6 Pedro
   void pickDorsal(1: i64 inscriptionId, 2: string creditCardNumber)
        throws (1: ThriftInputValidationException e1, 2: ThriftInstanceNotFoundException e2,
            3: ThriftDorsalPickedException e3, 4: ThriftWrongCreditCardException e4)
}