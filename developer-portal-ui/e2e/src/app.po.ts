import { browser, by, element } from 'protractor';

export class AppPage {
  navigateTo(url: string) {
    return browser.get(url);
  }

  getDevUrl() {
    return browser.getCurrentUrl();
  }

  getDescriptionTitle() {
    return element(by.css('h1')).getText();
  }

  clickDownloadButton() {
    element(by.css('.btn-primary')).click();
  }

  clickLanguageSwitcher() {
    element(by.css('.language-switcher')).click();
  }
}
