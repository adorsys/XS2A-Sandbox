/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

import { Injectable } from '@angular/core';
import { BehaviorSubject, interval, Subscription } from 'rxjs';

@Injectable({
  providedIn: 'root',
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
      this.subscriptions = this.timer.subscribe((time) =>
        this.timerSubject.next(time + ' 🙈')
      );
      this.tokenMonitoringInitialized = true;
    }
  }

  destroySubscription(): void {
    this.subscriptions.unsubscribe();
  }
}
