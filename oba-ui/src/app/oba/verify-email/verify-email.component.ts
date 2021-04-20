import { Component, OnInit, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { CustomizeService } from 'src/app/common/services/customize.service';

@Component({
  selector: 'app-verify-email',
  templateUrl: './verify-email.component.html',
  styleUrls: ['./verify-email.component.scss'],
})
export class VerifyEmailComponent implements OnInit {
  constructor(
    private router: Router,
    public customizeService: CustomizeService
  ) {}

  ngOnInit(): void {}

  hasRoute(route: string) {
    return this.router.url.includes(route);
  }
}
