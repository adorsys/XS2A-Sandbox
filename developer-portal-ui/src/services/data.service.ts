import {Injectable} from '@angular/core';
import {ToastrService} from 'ngx-toastr';

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
