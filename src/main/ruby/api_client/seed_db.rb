require 'rubygems'
require 'active_support'
require_relative 'api_client'
require 'faker'
require 'json'

def data_set_constructor resource_name, data_set = [] 
  resource_client = ApiClient::ResourceConsumer.new(resource_name)
  data_returned = []
  data_set.each do |data|
    data_returned << JSON.parse(resource_client.send(:post, data)).deep_symbolize_keys
  end
  data_returned
end

def relationship_constructor root_resource_name, root_resource_id, sub_resource_name, sub_resource
  ApiClient::ResourceConsumer::Worker.perform_async(root_resource_name, :post, "#{root_resource_id}/#{sub_resource_name}", sub_resource)
end

# basic data_sets

authors = data_set_constructor(:author, (Random.rand(20) + 1).times.inject([]) do |x,y|
  x << {firstName: Faker::Name.first_name, lastName: Faker::Name.last_name}
end)

directors = data_set_constructor(:director, (Random.rand(20) + 1).times.inject([]) do |x,y|
  x << {firstName: Faker::Name.first_name, lastName: Faker::Name.last_name}
end)

dvds = data_set_constructor(:dvd, (Random.rand(200) + 1).times.inject([]) do |x,y|
  x << {title: Faker::Lorem.sentence(3, true, 4), quantity: Faker::Number.number(3), price: Faker::Commerce.price}
end)

dvd_providers = data_set_constructor(:dvd_provider, (Random.rand(12) + 1).times.inject([]) do |x,y|
  x << {name: Faker::Company.name}
end)

dvd_orders = data_set_constructor(:dvd_order, (Random.rand(250) + 1).times.inject([]) do |x,y|
  x << {}
end)

# establishing relationship between dvds and authors, directors, dvd_providers

dvds.each do |dvd|
  author = authors[Random.rand(authors.size)]
  dvd_provider = dvd_providers[Random.rand(dvd_providers.size)]
  director = directors[Random.rand(directors.size)]
  [:author, :director, :dvd_provider].each do |resource|
    value = eval(resource.to_s)
    relationship_constructor resource, value[:id], :dvd, dvd
  end
end

# adding dvds to dvd_orders

dvd_orders.each do |dvd_order|
  (Random.rand(30) + 1).times do 
    dvd = dvds[Random.rand(dvds.size)]
    dvd[:quantity] = Random.rand(dvd[:quantity]) + 1
    relationship_constructor :dvd_order, dvd_order[:id], :dvd, dvd
  end
end

# posting the payment of dvd_orders

dvd_orders.each do |dvd_order|
  ApiClient::ResourceConsumer::Worker.perform_async(:dvd_order, :post, "#{dvd_order[:id]}/payment", {})
end