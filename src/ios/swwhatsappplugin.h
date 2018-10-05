//
//  swwhatsappplugin.h
//  Yaari
//
//  Created by Ravi Maheshwari on 30/06/18.
//

#import <Cordova/CDVPlugin.h>
#import <Foundation/Foundation.h>

@interface swwhatsappplugin : CDVPlugin{
    
    NSMutableArray *smsListArray;
    int i;
}

- (void) sendMessage:(CDVInvokedUrlCommand*)command;
@end
