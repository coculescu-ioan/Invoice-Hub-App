import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { InvoiceService } from '../services/invoice.service';

@Component({
  selector: 'invoices',
  templateUrl: './invoices.component.html',
  styleUrl: './invoices.component.css'
})
export class InvoicesComponent {

    invoices: any[] = [];
    constructor(
        private router: Router, 
        private invoiceService: InvoiceService
    ) {}

    ngOnInit(): void {
        //this.invoices = this.invoiceService.getInvoices();
    }
}
