import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SidebarService {
  private isOpenSubject = new BehaviorSubject<boolean>(false);
  isOpen$ = this.isOpenSubject.asObservable();

  toggleSidebar(){
    this.isOpenSubject.next(!this.isOpenSubject.value);
  }

  setSidebarState(isOpen: boolean){
    this.isOpenSubject.next(isOpen);
  }
}
