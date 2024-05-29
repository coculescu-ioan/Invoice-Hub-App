import {Component, OnInit} from '@angular/core';
import {UserService} from "../services/user.service";
import {MatDialog} from "@angular/material/dialog";
import {DeleteUserDialogComponent} from "../dialog/delete-user-dialog.component";

@Component({
  selector: 'manage-users',
  templateUrl: './manage-users.component.html',
  styleUrl: './manage-users.component.css'
})
export class ManageUsersComponent implements OnInit {

  users: any[] = [];
  displayedColumns: string[] = ['id', 'username', 'email', 'role', 'enabled', 'actions'];
  constructor(
    private userService: UserService,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.fetchUsers();
  }

  openDeleteDialog(user: any): void {
    const dialogRef = this.dialog.open(DeleteUserDialogComponent, {
      width: '300px',
      data: { user: user },
      disableClose: true,
      hasBackdrop: true
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === 'yes') {
        this.deleteUser(user.id);
      }
    });
  }

  deleteUser(id: number): void {
    this.userService.deleteUser(id).subscribe(response => {
        this.fetchUsers();
    });
  }

  fetchUsers() {
    this.userService.getAllUsers().subscribe(response => {
      if (response) {
        this.users = response;
        console.log(this.users);
      }
    });
  }

  deleteDisabled(user: any) {
    return user.role === 'ADMIN';
  }
}
