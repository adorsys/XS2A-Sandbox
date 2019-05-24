import { PrettyJsonPipe } from './pretty-json.pipe';

describe('PrettyJsonPipe', () => {
  it('create an instance', () => {
    const pipe = new PrettyJsonPipe();
    expect(pipe).toBeTruthy();
  });
});
