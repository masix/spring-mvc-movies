/*
Copyright [2012] [Carlo P. Micieli]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.github.carlomicieli.converters;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

import com.github.carlomicieli.models.Movie;
import com.github.carlomicieli.services.MovieService;

public class MovieConverter implements Converter<String, Movie> {
	private MovieService movieService;
	
	@Autowired
	public MovieConverter(MovieService rollingStockService) {
		this.movieService = rollingStockService;
	}
	
	@Override
	public Movie convert(String source) {
		ObjectId id = new ObjectId(source);
		return this.movieService.findById(id);
	}
}