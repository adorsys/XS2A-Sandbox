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

  pathToFile = `./assets/i18n/en/contact.md`;
  showQuestionsComponent;
  showContactCard;

  constructor(public customizeService: CustomizeService,
              private languageService: LanguageService) {
    let theme: Theme;

    setInterval(() => { // TODO create single instance of this service and pull data only one time! https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox/issues/591
      theme = this.customizeService.getTheme();
      if (theme.contactInfo.img.indexOf('/') === -1) { // TODO do it in the Customize service https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox/issues/591
        theme.contactInfo.img =
          '../assets/UI' +
          (this.customizeService.isCustom() ? '/custom/' : '/') +
          theme.contactInfo.img;
      }

      this.contactInfo = theme.contactInfo;
      this.officesInfo = theme.officesInfo;

      const contactPageSettings = theme.pagesSettings.contactPageSettings;
      if (contactPageSettings) {
        this.enableQuestionsComponent(contactPageSettings.showQuestionsComponent);
        this.enableContactCard(contactPageSettings.showContactCard);

      }
    }, 100);
  }

  ngOnInit() {
    this.languageService.currentLanguage.subscribe(
      data => {
        this.pathToFile = `./assets/i18n/${data}/contact.md`;
      });
  }

  private enableQuestionsComponent(showQuestionsComponent: boolean) {
    this.showQuestionsComponent = !showQuestionsComponent ? showQuestionsComponent : true;
  }

  private enableContactCard(showContactCard: boolean) {
    this.showContactCard = !showContactCard ? showContactCard : true;
  }
}
