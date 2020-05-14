import {Component, OnInit} from '@angular/core';
import {TppUserService} from '../../services/tpp.user.service';
import {User} from '../../models/user.model';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  admin = false;

  constructor(private tppUserService: TppUserService) {
  }

  ngOnInit() {
    this.tppUserService.currentTppUser.subscribe(
      (user: User) => this.admin = user && user.userRoles.includes('SYSTEM'));
  }

}
