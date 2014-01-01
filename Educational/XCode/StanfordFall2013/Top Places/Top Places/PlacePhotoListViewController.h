//
//  PlacePhotoListViewController.h
//  Top Places
//
//  Created by Ethan Petuchowski on 12/31/13.
//  Copyright (c) 2013 Ethan Petuchowski. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "PhotoListViewController.h"

@interface PlacePhotoListViewController : PhotoListViewController

@property (nonatomic) NSString *city;
@property (nonatomic) NSString *place_id;


@end
