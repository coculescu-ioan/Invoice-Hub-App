import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { LoginRequest, SignupRequest, ForgetPasswordRequest, ChangePasswordRequest } from './auth-requests';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = 'http://localhost:8080/api/auth';
  private TOKEN_KEY = 'auth-token';

  constructor(private http: HttpClient) {}

  login(loginRequest: LoginRequest): Observable<HttpResponse<any>> {
    return this.http.post(`${this.baseUrl}/login`, loginRequest, { observe: 'response' }).pipe(
      tap((response: HttpResponse<any>) => {
        const authHeader = response.headers.get('Authorization');
        if (authHeader) {
          const token = authHeader.replace('Bearer ', '');
          sessionStorage.setItem(this.TOKEN_KEY, token);
          console.log('Token stored:', token);  // Debugging line
        } else {
          console.log('Authorization header not found');
        }
      })
    );
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

  logout(): void {
    sessionStorage.removeItem(this.TOKEN_KEY);
  }

  getToken(): string | null {
    const token = sessionStorage.getItem(this.TOKEN_KEY);
    console.log('Token retrieved:', token);  // Debugging line
    return token;
  }

  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }
}
