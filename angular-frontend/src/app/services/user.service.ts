import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private baseUrl = 'http://localhost:8080/api';
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

  getFiles(): Observable<any[]> {
    const token = this.authService.getToken();
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<any[]>(`${this.baseUrl}/file/getAll`, { headers });
  }

  getLastSessions(): Observable<any[]> {
    const token = this.authService.getToken();
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<any[]>(`${this.baseUrl}/file/lastSessions`, { headers });
  }

  deleteUser(id: number) {
    const token = this.authService.getToken();
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.delete<any>(`${this.baseUrl}/users/${id}`, {headers});
  }

  uploadFile(file: File): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('file', file);

    const token = this.authService.getToken();
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.post<any>(`${this.baseUrl}/file/upload`, formData, { headers });
  }

  getAllUsers(): Observable<any[]> {
    const token = this.authService.getToken();
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<any[]>(`${this.baseUrl}/users`, {headers});
  }
}

