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
    }
  };
  it('create an instance', () => {
    pipe = new PrettyJsonPipe();
    expect(pipe).toBeTruthy();
  });

});
