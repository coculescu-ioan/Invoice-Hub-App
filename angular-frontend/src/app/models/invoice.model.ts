export class Invoice {
    constructor(
      public invoiceNumber: string,
      public invoiceDate: string,
      public type: string,
      public currency: string,
      public taxPercent: number,
      public clientName: string,
      public clientId: string,
      public clientAddress: string,
      public supplierName: string,
      public supplierId: string,
      public supplierAddress: string,
      public taxExclusiveAmount: number,
      public taxAmount: number,
      public taxInclusiveAmount: number
    ) {}
  }
  