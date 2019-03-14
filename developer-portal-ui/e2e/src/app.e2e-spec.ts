import { AppPage } from './app.po';
import { browser } from 'protractor';
import * as fs from 'fs';

describe('Certificate Service UI', () => {
  let page: AppPage;
  const filename = 'psu_cert.zip';

  function cleanUpDownloads() {
    if (fs.existsSync(filename)) {
      fs.unlinkSync(filename);
    }
  }

  beforeEach(() => {
    page = new AppPage();
    cleanUpDownloads();
  });

  afterEach(() => {
    cleanUpDownloads();
  });

  it('should check the developer portal url', () => {
    page.navigateTo('/');
    page.getDevUrl().then(url => {
      expect(url).toEqual('http://localhost:4200/app/developer-portal');
    });
  });

  it('should check certificate service headline', () => {
    page.navigateTo('app/certificate-service');
    expect(page.getDescriptionTitle()).toEqual('Certificate Service');
  });

  it('should check developer portal headline', () => {
    page.navigateTo('app/developer-portal');
    expect(page.getDescriptionTitle()).toEqual('Developer Portal');
  });

  it('should create a certificate', () => {
    page.navigateTo('app/certificate-service');
    page.clickDownloadButton();

    browser.driver
      .wait(() => {
        return fs.existsSync(filename);
      }, 3000)
      .then(() => {
        const file = fs.readFileSync(filename, { encoding: 'utf8' });
        expect(file).toContain('-----END RSA PRIVATE KEY-----');
        expect(file).toContain('-----BEGIN CERTIFICATE-----');
      });
  });

  it('should change language of content', () => {
    page.navigateTo('app/certificate-service');
    expect(page.getDescriptionTitle()).toEqual('Certificate Service');
    page.clickLanguageSwitcher();
    expect(page.getDescriptionTitle()).toEqual('Zertifikatsservice');
  });
});
