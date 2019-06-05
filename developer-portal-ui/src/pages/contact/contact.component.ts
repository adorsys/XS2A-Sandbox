import { Component, OnInit } from '@angular/core';
import {
  ContactInfo,
  CustomizeService,
  OfficeInfo,
} from '../../services/customize.service';

@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.scss'],
})
export class ContactComponent implements OnInit {
  contactInfo: ContactInfo;
  officesInfo: OfficeInfo[];

  constructor(public customizeService: CustomizeService) {}

  ngOnInit() {
    const theme = this.customizeService.getTheme();
    this.contactInfo = theme.contactInfo;
    this.officesInfo = theme.officesInfo;
  }
}
