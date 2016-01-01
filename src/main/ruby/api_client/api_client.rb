require 'rest-client'
require 'sidekiq'

module ApiClient

  API_BASE_URL = "http://localhost:8080/vod-2/api/"
  
  class ResourceConsumer
    
    class Worker 
      include Sidekiq::Worker
      def perform(resource_name, method, url_suffix, data)
        resource_client = ApiClient::ResourceConsumer.new(resource_name)
        resource_client.send(method, data, url_suffix)
      end
      
    end
    
    attr_reader :resource_name
    
    def initialize resource_name
      @logger = Logger.new(STDERR)
      @resource_name = resource_name
      @url = "#{ApiClient::API_BASE_URL}#{resource_name}/"
      @resource = RestClient::Resource.new @url
    end
    
    def send(method, data = {}, url_suffix = nil)
      begin
        resource = @resource[url_suffix.to_s]
        if [:post, :put, :patch].include? method.to_sym
          return resource.__send__(method.to_sym, data.to_json, content_type: :json, accept: :json)
        else
          return resource.__send__(method.to_sym, accept: :json)
        end
      rescue RestClient::InternalServerError => e
        @logger.error @resource_name
        @logger.error method
        @logger.error url_suffix
        @logger.error data
        raise e
      end
    end
    
  end
  
end