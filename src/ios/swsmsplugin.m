/*
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 */

//
//  MainViewController.h
//  HelloWorld
//
//  Created by ___FULLUSERNAME___ on ___DATE___.
//  Copyright ___ORGANIZATIONNAME___ ___YEAR___. All rights reserved.
//

#import "swsmsplugin.h"


@implementation swsmsplugin


-(void) sendSMS:(CDVInvokedUrlCommand*)command{
    @try {
        
        mCommand = command;
        if(command.arguments == NULL || command.arguments.count == 0){
            NSLog(@"Arguments not passed");
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Arguments not found"];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            return;
        }
        NSDictionary *dictionaryParams = [command.arguments objectAtIndex:0];
        if(dictionaryParams == NULL || dictionaryParams.count == 0){
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Pass Parameters"];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            return;
        }
        
        userList = [dictionaryParams objectForKey:@"sms_list"];
        //{"sms_list":[{"number":"", "message":"Hello"}]};
        if(userList != nil && userList.count > 0){
            currentIndex = 0;
            finalUserList = [[NSMutableArray alloc] init];
            [self sendSMSToContacts:userList];
        }
    }@catch (NSException *exception) {
        NSLog(@"KotakPlugin::gettouchId:: %@",[exception description]);
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[exception description]];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }
}
#pragma mark : SEND SMS
-(void)sendSMSToContacts:(NSArray *)smsList{
    @try {
        NSString * phoneNo = [[smsList objectAtIndex:currentIndex] objectForKey:@"number"];
        NSString * message = [[smsList objectAtIndex:currentIndex] objectForKey:@"message"];
        if([self checkIsMessageServiceAvailable]){
            mComposeVC = [[MFMessageComposeViewController alloc] init];
            mComposeVC.messageComposeDelegate = self;
            mComposeVC.recipients = @[phoneNo];
            mComposeVC.body = message;
            [self.viewController presentViewController:mComposeVC animated:YES completion:nil];
        }else{
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"SMS service not availabel"];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:mCommand.callbackId];
            return;
        }
    } @catch (NSException *exception) {
        NSLog(@"ERROR @ IBSMSPlugin :: sendSMSToContacts : %@",exception.description);
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[exception description]];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:mCommand.callbackId];
        return;
    }
}

-(BOOL) checkIsMessageServiceAvailable{
    @try {
        if (![MFMessageComposeViewController canSendText]) {
            NSLog(@"Message services are not available.");
            return false;
        }
        return true;
    } @catch (NSException *exception) {
        NSLog(@"ERROR @ SMSPlugin::checkIsMessageServiceAvailable : %@",exception.description);
    }
    return false;
}

#pragma mark - MF Compose SMS Delegets
- (void)messageComposeViewController:(MFMessageComposeViewController *)controller
                 didFinishWithResult:(MessageComposeResult)result {
    // Check the result or perform other tasks.    // Dismiss the message compose view controller.
    [mComposeVC dismissViewControllerAnimated:YES completion:nil];
    NSString * status = @"SENT";
    if(result == MessageComposeResultCancelled){
        NSLog(@"MESSAGE CANCELLED");
        status = @"CANCELLED";
    }else if(result == MessageComposeResultSent){
        NSLog(@"MESSAGE SENT");
        status = @"SENT";
    }else if(result == MessageComposeResultFailed){
        NSLog(@"MESSAGE FAILED");
        status = @"FAILED";
    }else{
        NSLog(@"UNKNOWN TYPE");
        status = @"UNKNOWN";
    }
    NSMutableDictionary * jsonObject = [userList objectAtIndex:currentIndex];
    [jsonObject setValue:status forKey:@"status"];
    [finalUserList addObject:jsonObject];
    
    [self performSelector:@selector(sendSMS) withObject:nil afterDelay:0.5f];
}

-(void) sendSMS{
    currentIndex = currentIndex + 1;
    if(currentIndex < userList.count){
        [self sendSMSToContacts:userList];
    }else{
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:[finalUserList copy]];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:mCommand.callbackId];
        return;
    }
}

@end
