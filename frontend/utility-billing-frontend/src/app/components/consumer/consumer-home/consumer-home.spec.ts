import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsumerHome } from './consumer-home';

describe('ConsumerHome', () => {
  let component: ConsumerHome;
  let fixture: ComponentFixture<ConsumerHome>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsumerHome]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConsumerHome);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
