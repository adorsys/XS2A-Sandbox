import { PrettyJsonPipe } from './pretty-json.pipe';

describe('PrettyJsonPipe', () => {
  let pipe: PrettyJsonPipe;
  const mockData = {
    globalSettings: {
      logo: 'Logo_XS2ASandbox.png',
      fontFamily: 'Arial, sans-serif',
      headerBG: '#ffffff',
      headerFontColor: '#000000',
      footerBG: '#054f72',
      footerFontColor: '#ffffff',
      facebook: 'https://www.facebook.com/adorsysGmbH/',
      linkedIn: 'https://www.linkedin.com/company/adorsys-gmbh-&-co-kg/',
    },
    contactInfo: {
      img: 'Rene.png',
      name: 'René Pongratz',
      position: 'Software Architect & Expert for API Management',
      email: 'psd2@adorsys.de',
    },
    officesInfo: [
      {
        city: 'Nürnberg',
        company: 'adorsys GmbH & Co. KG',
        addressFirstLine: 'Fürther Str. 246a, Gebäude 32 im 4.OG',
        addressSecondLine: '90429 Nürnberg',
        phone: '+49(0)911 360698-0',
        email: 'psd2@adorsys.de',
      },
      {
        city: 'Frankfurt',
        company: 'adorsys GmbH & Co. KG',
        addressFirstLine: 'Frankfurter Straße 63 - 69',
        addressSecondLine: '65760 Eschborn',
        email: 'frankfurt@adorsys.de',
        facebook: 'https://www.facebook.com/adorsysGmbH/',
        linkedIn: 'https://www.linkedin.com/company/adorsys-gmbh-&-co-kg/',
      },
    ],
  };
  it('create an instance', () => {
    pipe = new PrettyJsonPipe();
    expect(pipe).toBeTruthy();
  });

  it('should create JSON', () => {
    const afterTransform = pipe.transform(mockData);

    expect(typeof JSON.parse(afterTransform)).toBe('object');
  });
});
