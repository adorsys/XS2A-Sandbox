import {Component, OnInit} from '@angular/core';
import {CustomizeService} from '../../services/customize.service';
import {ContactInfo, OfficeInfo, Theme} from "../../models/theme.model";
import {LanguageService} from "../../services/language.service";

@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.scss'],
})
export class ContactComponent implements OnInit {
  contactInfo: ContactInfo;
  officesInfo: OfficeInfo[];

  pathToFile = `./assets/content/i18n/en/contact.md`;
  showQuestionsComponent;
  showContactCard;

  constructor(public customizeService: CustomizeService,
              private languageService: LanguageService) {
    if (this.customizeService.currentTheme) {
      this.customizeService.currentTheme
        .subscribe(theme => {
          this.contactInfo = theme.contactInfo;
          this.officesInfo = theme.officesInfo;

          const contactPageSettings = theme.pagesSettings.contactPageSettings;
          if (contactPageSettings) {
            this.enableQuestionsComponent(contactPageSettings.showQuestionsComponent);
            this.enableContactCard(contactPageSettings.showContactCard);
          }
        });
    }

  }

  ngOnInit() {
    this.languageService.currentLanguage.subscribe(
      data => {

        this.pathToFile = `${this.customizeService.currentLanguageFolder}/${data}/contact.md`;
      });
  }

  private enableQuestionsComponent(showQuestionsComponent: boolean) {
    this.showQuestionsComponent = !showQuestionsComponent ? showQuestionsComponent : true;
  }

  private enableContactCard(showContactCard: boolean) {
    this.showContactCard = !showContactCard ? showContactCard : true;
  }
}
