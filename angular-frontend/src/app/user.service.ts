import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

import { AuthService } from './auth/auth.service';  

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly USERNAME_KEY = 'username';

  constructor(private http: HttpClient, private authService: AuthService) {
    this.username = sessionStorage.getItem(this.USERNAME_KEY) || '';
  }

  private username: string = '';

  setUsername(username: string) {
    this.username = username;
    sessionStorage.setItem(this.USERNAME_KEY, username);
  }

  getUsername(): string {
    return this.username;
  }

  getUploadSessions(limit: number): Observable<any[]> {
    const token = this.authService.getToken();  // Use AuthService to get the token
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<any[]>(`/api/file/uploadSessions?limit=${limit}`, { headers });
  }
}
