import {Component, OnInit} from '@angular/core';
import {ContactInfo, CustomizeService, OfficeInfo, Theme} from '../../services/customize.service';

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
    let theme: Theme;
    theme = this.customizeService.getTheme();
    if (theme.contactInfo.img.indexOf('/') === -1) {
      theme.contactInfo.img =
        '../assets/UI' +
        (this.customizeService.isCustom() ? '/custom/' : '/') +
        theme.contactInfo.img;
    }
    this.contactInfo = theme.contactInfo;
    this.officesInfo = theme.officesInfo;
  }
}
