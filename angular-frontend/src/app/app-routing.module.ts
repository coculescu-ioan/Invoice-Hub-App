import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { ForgetPasswordComponent } from './forget-password/forget-password.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { InvoiceUploadComponent } from './invoice-upload/invoice-upload.component';
import {ManageUsersComponent} from "./manage-users/manage-users.component";
import {InvoicesComponent} from "./invoices/invoices.component";
import {FilesComponent} from "./files/files.component";

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'forget-password', component: ForgetPasswordComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'api/file/upload', component: InvoiceUploadComponent },
  { path: 'invoices', component: InvoicesComponent },
  { path: 'files', component: FilesComponent },
  { path: 'manageUsers', component: ManageUsersComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
