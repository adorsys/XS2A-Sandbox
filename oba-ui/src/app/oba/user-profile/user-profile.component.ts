import { Component, OnInit } from '@angular/core';
import { OnlineBankingService } from '../../common/services/online-banking.service';
import { UserTO } from '../../api/models/user-to';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss'],
})
export class UserProfileComponent implements OnInit {
  public obaUser: UserTO;

  constructor(private onlineBankingService: OnlineBankingService) {}

  ngOnInit() {
    this.getUserInfo();
  }

  public getUserInfo() {
    this.onlineBankingService.getCurrentUser().subscribe((data) => {
      if (data.body !== undefined && data.body !== null) {
        return (this.obaUser = data.body);
      }
    });
  }
}
