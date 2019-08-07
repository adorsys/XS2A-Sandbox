import { Injectable } from '@angular/core';
import {BehaviorSubject, interval, Subscription} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AutoLogoutService {

  // check every minute if token is valid. --> look navbar.component.ts
  timer = interval(60000);
  timerSubject;
  subscriptions = new Subscription();
  tokenMonitoringInitialized = false;


  constructor() {
    this.initializeTokenMonitoring();
  }

  resetMonitoringConfig(): void {
    this.tokenMonitoringInitialized = false;
    this.destroySubscription();
  }

  initializeTokenMonitoring(): void {
    if (!this.tokenMonitoringInitialized) {
      this.timerSubject = new BehaviorSubject('👌🏼');
      this.subscriptions = this.timer.subscribe(time => this.timerSubject.next(time + ' 🙈'));
      this.tokenMonitoringInitialized = true;
    }
  }

  destroySubscription(): void {
    this.subscriptions.unsubscribe();
  }
}
