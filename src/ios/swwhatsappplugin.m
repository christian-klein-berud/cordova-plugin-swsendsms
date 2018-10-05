//
//  swwhatsappplugin.m
//  Yaari
//
//  Created by Ravi Maheshwari on 30/06/18.
//

#import "swwhatsappplugin.h"

@implementation swwhatsappplugin

- (void) sendMessage:(CDVInvokedUrlCommand*)command{
    @try {
        
        if(command.arguments == NULL || command.arguments.count == 0){
            NSLog(@"Arguments not passed");
            [self sendResultToPluginWithMessage:@"Arguments not found" status:CDVCommandStatus_ERROR andCallBackId:command.callbackId];
            return;
        }
        
        NSDictionary *dictionaryParams = [command.arguments objectAtIndex:0];
        if(dictionaryParams == NULL || dictionaryParams.count == 0){
            NSLog(@"Parameters does not passed");
            [self sendResultToPluginWithMessage:@"Pass Parameters" status:CDVCommandStatus_ERROR andCallBackId:command.callbackId];
            return;
        }
        
        smsListArray = [[NSMutableArray alloc]init];
        smsListArray =[dictionaryParams objectForKey:@"sms_list"];

        NSString * phoneNo = [[[dictionaryParams objectForKey:@"sms_list"] objectAtIndex:0] objectForKey:@"number"];
        NSString *message = [[[dictionaryParams objectForKey:@"sms_list"] objectAtIndex:0] objectForKey:@"message"];
        i = 0;
        if (phoneNo.length==10)
        {
            phoneNo =[NSString stringWithFormat:@"+91%@", phoneNo];
        }
        else if ([phoneNo hasPrefix:@"0"] && [phoneNo length] == 11) {
            NSRange range = NSMakeRange(0,1);
            phoneNo = [phoneNo stringByReplacingCharactersInRange:range withString:@"+91"];
        }
        NSString * urlSchema = [[NSString stringWithFormat:@"whatsapp://send?phone=%@&text=%@",phoneNo, message]  stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLFragmentAllowedCharacterSet]];

        NSURL *whatsappURL = [NSURL URLWithString:urlSchema];
        if ([[UIApplication sharedApplication] canOpenURL: whatsappURL]) {
            [[UIApplication sharedApplication] openURL: whatsappURL];
            [self sendResultToPluginWithMessage:@"SENT" status:CDVCommandStatus_ERROR andCallBackId:cDVInvokedUrlCommand.callbackId];

        }else{
            [self sendResultToPluginWithMessage:@"WhatsApp is not installed in your device!" status:CDVCommandStatus_ERROR andCallBackId:command.callbackId];
            return;
        }
        cDVInvokedUrlCommand = command;
        NSNotificationCenter* nc = [NSNotificationCenter defaultCenter];
        [nc addObserver:self selector:@selector(viewDidEnterForeground) name:UIApplicationWillEnterForegroundNotification object:nil];

    } @catch (NSException *exception) {
        NSLog(@"ERROR @ MessengerPlugin::sendWhatsAppMessage : %@",exception.description);
    }
}
-(void)viewDidEnterForeground{
    [self performSelector:@selector(openWhatsapp) withObject:nil afterDelay:0.5];
    
}
-(void)openWhatsapp
{
    i++;
    if (smsListArray.count>i)
    {
        NSString * phoneNo = [[smsListArray objectAtIndex:i] objectForKey:@"number"];
        NSString *message = [[smsListArray objectAtIndex:i] objectForKey:@"message"];
        
        if (phoneNo.length==10)
        {
            phoneNo =[NSString stringWithFormat:@"+91%@", phoneNo];
        }
        else if ([phoneNo hasPrefix:@"0"] && [phoneNo length] == 11) {
            NSRange range = NSMakeRange(0,1);
            phoneNo = [phoneNo stringByReplacingCharactersInRange:range withString:@"+91"];
        }
        
        
        NSString * urlSchema = [[NSString stringWithFormat:@"whatsapp://send?phone=%@&text=%@",phoneNo, message]  stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLFragmentAllowedCharacterSet]];
        NSURL *whatsappURL = [NSURL URLWithString:urlSchema];
        [[UIApplication sharedApplication] openURL: whatsappURL];
    }
    [self sendResultToPluginWithMessage:@"SENT" status:CDVCommandStatus_ERROR andCallBackId:cDVInvokedUrlCommand.callbackId];

   
    NSNotificationCenter* nc = [NSNotificationCenter defaultCenter];
    [nc removeObserver:UIApplicationWillEnterForegroundNotification name:UIApplicationWillEnterForegroundNotification object:nil];
    
}

-(void)sendResultToPluginWithMessage:(NSString *)message status:(CDVCommandStatus)status andCallBackId:(NSString *)callBackId{
    @try {
        CDVPluginResult *pluginResult =[CDVPluginResult resultWithStatus:status messageAsString:message];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:callBackId];
    }
    @catch (NSException *exception) {
        NSLog(@"Error swwhatsappplugin::sendResultToPluginWithMessage: %@",exception.description);
    }
}


@end
