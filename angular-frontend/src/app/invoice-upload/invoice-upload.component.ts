import { Component } from '@angular/core';
import { Invoice } from '../models/invoice.model';
import { UserService } from '../services/user.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-invoice-upload',
  templateUrl: './invoice-upload.component.html',
  styleUrls: ['./invoice-upload.component.css']
})
export class InvoiceUploadComponent {
  invoice: Invoice = new Invoice('', '', '', '', 0, '', '', '', '', '', '', 0, 0, 0);
  selectedFile: File | undefined;
  public fileUploadEnabled: boolean = true;
  public errorMessage: string | null = null;

  constructor(private userService: UserService) { }

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  uploadInvoice() {
    if (this.selectedFile) {
      this.fileUploadEnabled = false; // Disable further uploads until this is processed
      this.errorMessage = null; // Clear previous error message

      this.userService.uploadFile(this.selectedFile).subscribe({
        next: (response) => {
          console.log('File uploaded successfully', response);
          this.fileUploadEnabled = true; // Re-enable the upload
        },
        error: (error: HttpErrorResponse) => {
          console.error('Error uploading file', error);
          this.fileUploadEnabled = true; // Re-enable the upload
          if (error.status === 400 && error.error) {
            this.errorMessage = error.error; // Capture the error message from the response
          } else {
            this.errorMessage = 'An unexpected error occurred. Please try again later.';
          }
        }
      });

      console.log('Uploaded Invoice');
      console.log('Invoice Number:', this.invoice.invoiceNumber);
      console.log('Invoice Date:', this.invoice.invoiceDate);
      console.log('Selected File:', this.selectedFile);
    } else {
      console.error('No file selected');
      this.errorMessage = 'No file selected';
    }
  }
}
