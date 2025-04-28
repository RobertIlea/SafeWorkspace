import { Component, NgZoneOptions, OnInit, ViewChild } from '@angular/core';
import { BreakpointObserver } from '@angular/cdk/layout';
import { MatSidenav } from '@angular/material/sidenav';
import { SidebarService } from '../../services/sidebar.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: false,
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent implements OnInit{
  isOpen = true;  
  userData: any = null;
  constructor(private sidebarService: SidebarService, private router: Router){}
  ngOnInit(): void {
    const storedUser = sessionStorage.getItem('user');
    if(storedUser){
      const parsedUser = JSON.parse(storedUser);
      this.userData = parsedUser.user_data;
      console.log("User data laoded:" , this.userData);
    }
  }

  logout() {
    localStorage.removeItem('jwtToken');
    sessionStorage.removeItem('user');

    this.router.navigate(['/login']);
  }
}
