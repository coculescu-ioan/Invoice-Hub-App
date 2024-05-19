import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoginRequest, SignupRequest, ForgetPasswordRequest, ChangePasswordRequest } from './auth-requests';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  login(loginRequest: LoginRequest): Observable<any> {
    return this.http.post(`${this.baseUrl}/login`, loginRequest);
  }

  register(signupRequest: SignupRequest): Observable<any> {
    return this.http.post(`${this.baseUrl}/register`, signupRequest);
  }

  forgetPassword(forgetPasswordRequest: ForgetPasswordRequest): Observable<any> {
    return this.http.post(`${this.baseUrl}/forget-password`, forgetPasswordRequest);
  }

  changePassword(changePasswordRequest: ChangePasswordRequest): Observable<any> {
    return this.http.post(`${this.baseUrl}/change-password`, changePasswordRequest);
  }
}
