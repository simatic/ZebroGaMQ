//
//  ViewController.m
//  ios-application
//
//  Created by Van Hung LE on 08/11/2013.
//  Copyright (c) 2013 ZebroGamQ. All rights reserved.
//

#import "ViewController.h"
#import "GameLogicApplication.h"
#import <zebrogamq-ios/Properties.h>
#import <zebrogamq-ios/ZebroGamqUtil.h>
#import <objc/runtime.h>

@interface ViewController ()

// Text View contains results of C++ code
@property (weak, nonatomic) IBOutlet UITextView *resultTextView;

// Path of properties files
@property (weak, nonatomic) NSString *xmlrpcPropertiesFilePath;
@property (weak, nonatomic) NSString *rabbitmqPropertiesFilePath;
@property (weak, nonatomic) NSString *configPropertiesFilePath;

// Lo file containing output result
@property (weak, nonatomic) NSString *logFilePath;

// Attribute to check the properties loading is ok
@property (nonatomic) BOOL loadOK;

// Attribute to check the C++ XMLRPC login is OK
@property (nonatomic) BOOL loggedIn;

// Attribute to check the ObjC XMLRPC login is OK
@property (nonatomic) BOOL xmlrpcConnected;
@property(nonatomic,retain) IBOutlet UIActivityIndicatorView *indicator;

@end

@implementation ViewController

NSString * const PLAYER_1 = @"PLAYER_1";
NSString * const PLAYER_2 = @"PLAYER_2";
NSString * const DEFAULT_PASSWORD = @"ufGf64";
NSString * const DEFAULT_GAMENAME = @"Tidy-City";
NSString * const DEFAULT_INSTANCENAME = @"Instance-1";

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    
    // Setup the indicator view
    self.indicator = [[UIActivityIndicatorView alloc]initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
    self.indicator.frame = CGRectMake(0.0, 0.0, 60.0, 60.0);
    CGAffineTransform transform = CGAffineTransformMakeScale(1.5f, 1.5f);
    self.indicator.transform = transform;
    self.indicator.center = self.view.center;
    [self.view addSubview:self.indicator];
    [self.indicator bringSubviewToFront:self.view];
    self.indicator.hidesWhenStopped = YES;
    
    self.xmlrpcConnected = false;
    
    // Get properties paths (from resource files of project)
    self.xmlrpcPropertiesFilePath = [[NSBundle mainBundle]
                                          pathForResource:@"xmlrpc"
                                          ofType:@"properties"];
    self.rabbitmqPropertiesFilePath = [[NSBundle mainBundle]
                                            pathForResource:@"rabbitmq"
                                            ofType:@"properties"];
    self.configPropertiesFilePath = [NSTemporaryDirectory() stringByAppendingPathComponent:@"config.properties"];
    
    self.logFilePath = [NSTemporaryDirectory() stringByAppendingPathComponent:@"Log.log"];
    std::string xmlrpcPropertiesFile = std::string( [[self xmlrpcPropertiesFilePath ] UTF8String] );
    std::string rabbitmqPropertiesFile = std::string( [[self rabbitmqPropertiesFilePath] UTF8String] );
    std::string configPropertiesFile = std::string( [[self configPropertiesFilePath ] UTF8String] );
    
    // Set new path for log file (in temporary folder of application)
    GameLogicApplication::updateConfigProperties("temporaryLogFile", std::string([self.logFilePath UTF8String]), configPropertiesFile);
    
    // load properties files
    self.loadOK = GameLogicApplication::loadProperties(xmlrpcPropertiesFile, rabbitmqPropertiesFile, configPropertiesFile);
    
    // Each time log file above is written, this method is called
    std::string filename = std::string([self.logFilePath UTF8String]);
    [self MonitorNameChangesToFile: filename.c_str()];
}

/**
 * TODO: Each time log file is written, this method is called (log file contains all output of C++ code). It reads content of log file then update into Text View
 */
- (dispatch_source_t) MonitorNameChangesToFile:(const char*) filename
{
    int fd = open(filename, O_EVTONLY);
    if (fd == -1)
        return NULL;
    
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    dispatch_source_t source = dispatch_source_create(DISPATCH_SOURCE_TYPE_VNODE,
                                                      fd, DISPATCH_VNODE_WRITE, queue);
    if (source)
    {
        // Copy the filename for later use.
        int length = strlen(filename);
        char* newString = (char*)malloc(length + 1);
        newString = strcpy(newString, filename);
        dispatch_set_context(source, newString);
        
        // Install the event handler to process the file written
        dispatch_source_set_event_handler(source, ^{
            const char*  oldFilename = (char*)dispatch_get_context(source);
            NSString* fileContent = [self readFile:[NSString stringWithFormat:@"%s", oldFilename]];
            dispatch_async(dispatch_get_main_queue(), ^ {
                //Stop your activity indicator or anything else with the GUI
                //Code here is run on the main thread
                [self updateTextView: fileContent];
            });
        });
        
        // Install a cancellation handler to free the descriptor
        // and the stored string.
        dispatch_source_set_cancel_handler(source, ^{
            char* fileStr = (char*)dispatch_get_context(source);
            free(fileStr);
            close(fd);
        });
        
        // Start processing events.
        dispatch_resume(source);
    }
    else
        close(fd);
    
    return source;
}

/*
 * TODO: Append new text in the end of Text View
 */
- (void) updateTextView: (NSString *) text {
    NSString* textView = [[self resultTextView] text];
    [[self resultTextView] setText:[NSString stringWithFormat:@"%@ %@", textView, text]];
    [self.resultTextView scrollRangeToVisible:NSMakeRange([self.resultTextView.text length], 0)];
}

/*
 * TODO: Create Instance (called when Create Instance button is pressed)
 */
- (IBAction)createInstance:(id)sender {
    [[self resultTextView] setText:(@"Creating Instance ...\n\n")];
    [self.indicator startAnimating];
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        //Call your function or whatever work that needs to be done
        //Code in this part is run on a background thread
        
        if (self.loadOK) {
            // instantiate GameLogicState
            GameLogicApplication::state = new GameLogicState();
            GameLogicApplication::state->login = std::string( [PLAYER_1 UTF8String] );
            GameLogicApplication::state->password = std::string( [DEFAULT_PASSWORD UTF8String] );
            GameLogicApplication::state->gameName = std::string( [DEFAULT_GAMENAME UTF8String] );
            GameLogicApplication::state->instanceName = std::string( [DEFAULT_INSTANCENAME UTF8String] );
            
            // Check XMLRPC connection in ObjC
            NSString* host = [NSString stringWithUTF8String:ZebroGamqUtil::getXMLRPCProperties()->getProperty("gameServerXMLRPCHost").c_str()];
            NSString* port = [NSString stringWithUTF8String:ZebroGamqUtil::getXMLRPCProperties()->getProperty("gameServerXMLRPCPort").c_str()];
            self.xmlrpcConnected = [self checkXMLRPCConnection: host : port ];
            if (!self.xmlrpcConnected) {
                dispatch_async(dispatch_get_main_queue(), ^ {
                    [self.indicator stopAnimating];
                    [self updateTextView:@"Instance is not created. Bad XML-RPC answer."];
                });
                return;
            }
            
            // execute the XMLRPC call
            self.loggedIn = false;
            self.loggedIn = GameLogicApplication::executeXMLRPCLogin("createAndJoinGameInstance");
            if (self.xmlrpcConnected && self.loggedIn) {
                // Stop the indicator
                dispatch_async(dispatch_get_main_queue(), ^ {
                    [self.indicator stopAnimating];
                });
                GameLogicApplication::initChannelsManager();
                // launch the participant list thread (except for spectators applications
                if ( GameLogicApplication::state->role.compare(GameLogicState::SPECTATOR) != 0 ) {
                    GameLogicApplication::startParticipantListThread();
                }
            } else {
                // Stop the indicator
                dispatch_async(dispatch_get_main_queue(), ^ {
                    [self.indicator stopAnimating];
                    [self updateTextView:@"Instance is not created. Bad XML-RPC answer."];
                });
            }
        } else {
            // Stop the indicator
            dispatch_async(dispatch_get_main_queue(), ^ {
                [self.indicator stopAnimating];
                [self updateTextView:@"Properties files have not been loaded."];
            });
        }
    });
}

/*
 * TODO: Join Instance (called when Join Instance button is pressed)
 */
- (IBAction)joinInstance:(id)sender {
    [[self resultTextView] setText:(@"Joining Instance ...\n\n")];
    [self.indicator startAnimating];
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        //Call your function or whatever work that needs to be done
        //Code in this part is run on a background thread
        if (self.loadOK) {
            // instantiate GameLogicState
            GameLogicApplication::state = new GameLogicState();
            GameLogicApplication::state->login = std::string( [PLAYER_2 UTF8String] );
            GameLogicApplication::state->password = std::string( [DEFAULT_PASSWORD UTF8String] );
            GameLogicApplication::state->gameName = std::string( [DEFAULT_GAMENAME UTF8String] );
            GameLogicApplication::state->instanceName = std::string( [DEFAULT_INSTANCENAME UTF8String] );
            
            // Check XMLRPC connection
            NSString* host = [NSString stringWithUTF8String:ZebroGamqUtil::getXMLRPCProperties()->getProperty("gameServerXMLRPCHost").c_str()];
            NSString* port = [NSString stringWithUTF8String:ZebroGamqUtil::getXMLRPCProperties()->getProperty("gameServerXMLRPCPort").c_str()];
            self.xmlrpcConnected  = [self checkXMLRPCConnection: host : port ];
            if (!self.xmlrpcConnected) {
                dispatch_async(dispatch_get_main_queue(), ^ {
                    [self.indicator stopAnimating];
                    [self updateTextView:@"Instance is not joint. Bad XML-RPC answer."];
                });
                return;
            }
            
            // execute the XMLRPC call
            self.loggedIn = false;
            self.loggedIn = GameLogicApplication::executeXMLRPCLogin("joinGameInstance");
            if (self.xmlrpcConnected && self.loggedIn) {
                // Stop the indicator
                dispatch_async(dispatch_get_main_queue(), ^ {
                    [self.indicator stopAnimating];
                });
                GameLogicApplication::initChannelsManager();
                // launch the participant list thread (except for spectators applications
                if ( GameLogicApplication::state->role.compare(GameLogicState::SPECTATOR) != 0 ) {
                    GameLogicApplication::startParticipantListThread();
                }
            } else {
                // Stop the indicator
                dispatch_async(dispatch_get_main_queue(), ^ {
                    [self.indicator stopAnimating];
                    [self updateTextView:@"Instance is not joint. Bad XML-RPC answer."];
                });
            }
        } else {
            // Stop the indicator
            dispatch_async(dispatch_get_main_queue(), ^ {
                [self.indicator stopAnimating];
                [self updateTextView:@"Properties files have not been loaded."];
            });
        }
    });
    
}

/*
 * TODO: Check XMLRPC connection by sending one Html POST request to server. This method is needed because the XMLRPC connection by C++ crashes the application
 */
- (BOOL)checkXMLRPCConnection:(NSString*) host :(NSString*) port
{
    NSString *bodyData = @"<?xml version=\"1.0\"?>\r\n<methodCall><methodName>createAndJoinGameInstance</methodName>\r\n<params><param><value>TEST_PLAYER</value></param><param><value>TEST_PASSWORD</value></param><param><value>TEST_GAMENAME</value></param><param><value>TEST_INSTANCETAME</value></param></params></methodCall>\r\n";
    
    NSString * url = [NSString stringWithFormat:@"http://%@:%@/RPC2", host, port];
    NSMutableURLRequest *postRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
    
    // Set the request's header
    [postRequest setValue:@"text/xml" forHTTPHeaderField:@"Content-Type"];
    [postRequest setValue:@"XMLRPC++ 0.7" forHTTPHeaderField:@"User-Agent"];
    [postRequest setValue:@"293" forHTTPHeaderField:@"Content-length"];
    NSString * hostURL = [NSString stringWithFormat:@"%@:%@", host, port];
    [postRequest setValue:hostURL forHTTPHeaderField:@"Host"];
    
    // Designate the request a POST request and specify its body data
    [postRequest setHTTPMethod:@"POST"];
    [postRequest setHTTPBody:[NSData dataWithBytes:[bodyData UTF8String] length:strlen([bodyData UTF8String])]];
    
    // Release the request
    NSError *err=nil;
    NSData *responseData=[NSURLConnection sendSynchronousRequest:postRequest returningResponse:nil error:&err];
    
    return (responseData != nil);
}

/*
 * TODO: Read text file (used to read log file)
 */
- (NSString* )readFile:(NSString*) filename {
    NSString *contents = [NSString stringWithContentsOfFile:filename encoding:NSASCIIStringEncoding error:nil];
    return contents;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
