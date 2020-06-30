import { Injectable } from '@angular/core';
import { OnlineBankingAccountInformationService } from '../../api/services/online-banking-account-information.service';
import { UserTO } from '../../api/models/user-to';
import { OnlineBankingAuthorizationProvidesAccessToOnlineBankingService } from '../../api/services/online-banking-authorization-provides-access-to-online-banking.service';

@Injectable({
  providedIn: 'root',
})
export class CurrentUserService {
  constructor(
    private onlineBankingService: OnlineBankingAccountInformationService,
    private onlineBankingAuthorizationService: OnlineBankingAuthorizationProvidesAccessToOnlineBankingService
  ) {}
  public getCurrentUser() {
    return this.onlineBankingService.getCurrentAccountInfo();
  }

  public updateUserDetails(updatedUserDetails: UserTO) {
    return this.onlineBankingAuthorizationService.updateUserDetailsUsingPUT(
      updatedUserDetails
    );
  }
}
