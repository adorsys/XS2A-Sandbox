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

import { inject, TestBed } from '@angular/core/testing';
import { AutoLogoutService } from './auto-logout.service';

describe('Auto-Logout Service', () => {
  let autoLogoutService: AutoLogoutService;
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AutoLogoutService],
    });
    autoLogoutService = TestBed.inject(AutoLogoutService);
  });

  it('should be created', inject(
    [AutoLogoutService],
    (service: AutoLogoutService) => {
      expect(service).toBeTruthy();
    }
  ));

  it('should set boolean to ture after start monitoring', () => {
    autoLogoutService.initializeTokenMonitoring();
    expect(autoLogoutService.tokenMonitoringInitialized).toBe(true);
  });

  it('shoulddestroySubscribtions and set boolean to false', () => {
    const autoLogoutServicespy = spyOn(
      autoLogoutService.subscriptions,
      'unsubscribe'
    );
    autoLogoutService.resetMonitoringConfig();
    expect(autoLogoutServicespy).toHaveBeenCalled();
    expect(autoLogoutService.tokenMonitoringInitialized).toBe(false);
  });

  it('should unsubscribe all subscriptions', () => {
    const autoLogoutServicespy = spyOn(
      autoLogoutService.subscriptions,
      'unsubscribe'
    );
    autoLogoutService.destroySubscription();
    expect(autoLogoutServicespy).toHaveBeenCalled();
  });
});
