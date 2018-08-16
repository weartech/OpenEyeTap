from abc import ABC, abstractmethod

"""Abstract class for compliancy with Weartech communication hubs"""
class WeartechCommunicable(ABC):
    
    def __init__(self, server):
        super().__init__()
        self.server = server
        
        
    """Receive incoming data from self.server"""
    @abstractmethod
    def get_data(self, data):
        pass
    
    
    """Send data to self.server"""
    @abstractmethod
    def send_data(self, data):
        pass
