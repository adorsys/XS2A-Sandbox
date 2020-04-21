import { Component, OnInit } from '@angular/core';
import { CustomizeService } from '../../services/customize.service';
import { ContactInfo, OfficeInfo, Theme } from '../../models/theme.model';
import { LanguageService } from '../../services/language.service';

@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.scss'],
})
export class ContactComponent implements OnInit {
  contactInfo: ContactInfo;
  officesInfo: OfficeInfo[];

  pathToFile = `./assets/content/i18n/en/contact.md`;

  constructor(public customizeService: CustomizeService, private languageService: LanguageService) {
    if (this.customizeService.currentTheme) {
      this.customizeService.currentTheme.subscribe((theme: Theme) => {
        if (theme.pagesSettings) {
          const contactPageSettings = theme.pagesSettings.contactPageSettings;
          if (contactPageSettings) {
            this.setContactInfo(contactPageSettings.contactInfo);
            this.setOfficesInfo(contactPageSettings.officesInfo);
          }
        }
      });
    }
  }

  ngOnInit() {
    this.languageService.currentLanguage.subscribe((data) => {
      this.pathToFile = `${this.customizeService.currentLanguageFolder}/${data}/contact.md`;
    });
  }

  private setContactInfo(contactInfo: ContactInfo) {
    if (contactInfo) {
      this.contactInfo = contactInfo;
    }
  }

  private setOfficesInfo(officesInfo: OfficeInfo[]) {
    if (officesInfo) {
      this.officesInfo = officesInfo;
    }
  }
}
