import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';  
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { ForgetPasswordComponent } from './forget-password/forget-password.component';
import { DashboardComponent } from './dashboard/dashboard.component';

import { AuthService } from './auth/auth.service';  
import { AuthInterceptor } from './auth/auth-interceptor.service';
import { UserService } from './user.service';  

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    ForgetPasswordComponent,
    DashboardComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,  
    HttpClientModule
  ],
  providers: [
    AuthService,
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    UserService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
