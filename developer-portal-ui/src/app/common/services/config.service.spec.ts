import { TestBed } from '@angular/core/testing';

import { ConfigService } from './config.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ContentUrls } from '../../../models/contentUrls';

describe('ConfigService', () => {
  let service: ConfigService;
  const contentUrlsDe: ContentUrls = {
    cert: 'http://kunde.de/cert-de.md',
    faq: 'http://kunde.de/faq-de.md',
    portal: 'http://kunde.de/portal-de.md',
  };
  const contentUrlsEn: ContentUrls = {
    cert: 'http://kunde.de/cert-en.md',
    faq: 'http://kunde.de/faq-en.md',
    portal: 'http://kunde.de/portal-en.md',
  };
  const customerConfig = {
    contactMailto: 'testmail@mail.de',
    certPageEnabled: false,
    logoUrl: null,
    contentUrlsDe: contentUrlsDe,
    contentUrlsEn: contentUrlsEn,
  };

  beforeEach(() =>
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    })
  );

  beforeEach(() => {
    service = TestBed.get(ConfigService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should override default config with customer config', () => {
    service.setConfig(customerConfig);
    const config = service.getConfig();

    expect(config.logoUrl).toBe('assets/logo-accelerator.svg');
    expect(config.contentUrlsDe.cert).toBe('http://kunde.de/cert-de.md');
    expect(config.certPageEnabled).toBe(false);
  });

  it('should ignore empty string in customer config', () => {
    customerConfig.contactMailto = '';
    customerConfig.certPageEnabled = null;
    customerConfig.contentUrlsEn.faq = '';

    service.setConfig(customerConfig);
    const config = service.getConfig();

    expect(config.contactMailto).toBe('mailto:psd2@adorsys.de');
    expect(config.certPageEnabled).toBe(true);
    expect(config.contentUrlsEn.faq).toBe('assets/docs/en/faq-page.md');
  });
});
