import {Component} from '@angular/core';
import {ShareDataService} from "./common/services/share-data.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {

  public operation: string;

  constructor(private sharedService: ShareDataService) {
    this.sharedService.currentOperation.subscribe(operation => {
      this.operation = operation;
    })
  }

  public checkUrl(): number {
    const url = window.location.href;
    return url.indexOf('/login');
  }

  public checkUrlbank(): number {
    const url = window.location.href;
    return url.indexOf('/bank-offered');
  }
}
