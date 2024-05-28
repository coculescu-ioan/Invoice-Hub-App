import { Component, OnInit } from '@angular/core';
import { UserService } from '../services/user.service';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  username: string = '';
  userRole: string | null = '';
  uploadSessions: any[] = [];

  constructor(
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.username = this.userService.getUsername();
    this.userRole = sessionStorage.getItem("user-role");
    console.log('User role:', this.userRole);
    this.userService.getLastSessions().subscribe(
      response => {
        this.uploadSessions = response;
      },
      (error) => {
        console.error('Error fetching upload sessions:', error);
      }
    );
  }

  onUploadInvoice() {
    // Logic to open the invoice upload component or route to it
    this.router.navigate(['/api/file/upload']);
  }
}
