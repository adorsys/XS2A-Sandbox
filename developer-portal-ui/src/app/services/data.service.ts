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
import { ToastrService } from 'ngx-toastr';

@Injectable()
export class DataService {
  private isLoading = false;
  private currentRouteUrl = '';

  constructor(private toastr: ToastrService) {}

  showToast(message, title, type) {
    if (type === 'success') {
      this.toastr.success(message, title);
    } else {
      this.toastr.error(message, title);
    }
  }

  setIsLoading(value: boolean) {
    this.isLoading = value;
  }

  getIsLoading() {
    return this.isLoading;
  }

  setRouterUrl(val: string) {
    this.currentRouteUrl = val;
  }

  getRouterUrl(): string {
    return this.currentRouteUrl;
  }
}
