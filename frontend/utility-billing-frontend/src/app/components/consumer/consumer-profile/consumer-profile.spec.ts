import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsumerProfile } from './consumer-profile';

describe('ConsumerProfile', () => {
  let component: ConsumerProfile;
  let fixture: ComponentFixture<ConsumerProfile>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsumerProfile]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConsumerProfile);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
