import { Component, OnInit } from '@angular/core';
import { UserService } from '../user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  username: string = '';
  userRole: string = '';
  uploadSessions: any[] = [];

  constructor(
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.username = this.userService.getUsername();
    this.userRole = 'user'; // Replace with logic to fetch actual role

    const sessionLimit = this.userRole === 'admin' ? 6 : 3;
    this.userService.getUploadSessions(sessionLimit).subscribe(
      (sessions: any[]) => {
        this.uploadSessions = sessions;
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
