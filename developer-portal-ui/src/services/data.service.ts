import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

@Injectable()
export class DataService {
  isLoading = false;
  currentRouteUrl = '';

  constructor(private toastr: ToastrService) {}

  showToast(message, title, type) {
    if (type === 'success') {
      this.toastr.success(message, title);
    } else {
      this.toastr.error(message, title);
    }
  }
}
