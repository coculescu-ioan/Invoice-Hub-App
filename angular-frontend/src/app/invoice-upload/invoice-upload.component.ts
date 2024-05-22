import { Component } from '@angular/core';
import { Invoice } from '../models/invoice.model';

@Component({
  selector: 'app-invoice-upload',
  templateUrl: './invoice-upload.component.html',
  styleUrls: ['./invoice-upload.component.css']
})
export class InvoiceUploadComponent {
  invoice: Invoice = new Invoice('', '', '', '', 0, '', '', '', '', '', '', 0, 0, 0);
  selectedFile: File | undefined;

  constructor() { }

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  uploadInvoice() {
    // Implement upload logic here
    console.log('Uploaded Invoice');
    console.log('Invoice Number:', this.invoice.invoiceNumber);
    console.log('Invoice Date:', this.invoice.invoiceDate);
    console.log('Selected File:', this.selectedFile);
    // Add logic to send data to the backend
  }
}
