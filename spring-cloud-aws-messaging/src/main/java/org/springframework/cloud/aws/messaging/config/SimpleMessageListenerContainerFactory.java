/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.aws.messaging.config;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import org.springframework.cloud.aws.core.env.ResourceIdResolver;
import org.springframework.cloud.aws.messaging.listener.QueueMessageHandler;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.core.DestinationResolver;
import org.springframework.util.Assert;

/**
 * @author Alain Sahli
 * @since 1.0
 */
public class SimpleMessageListenerContainerFactory {

	private TaskExecutor taskExecutor;

	private Integer maxNumberOfMessages;

	private Integer visibilityTimeout;

	private Integer waitTimeOut;

	private boolean autoStartup = true;

	private AmazonSQSAsync amazonSqs;

	private QueueMessageHandler queueMessageHandler;

	private ResourceIdResolver resourceIdResolver;

	private DestinationResolver<String> destinationResolver;

	private Boolean deleteMessageOnException;

	private Long backOffTime;

	/**
	 * Configures the {@link TaskExecutor} which is used to poll messages and execute them
	 * by calling the handler methods.
	 * If no {@link TaskExecutor} is set, a default one is created.
	 *
	 * @param taskExecutor
	 * 		The {@link TaskExecutor} used by the container
	 * @see SimpleMessageListenerContainer#createDefaultTaskExecutor()
	 */
	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	/**
	 * Configure the maximum number of messages that should be retrieved during one poll to the Amazon SQS system. This
	 * number must be a positive, non-zero number that has a maximum number of 10. Values higher then 10 are currently
	 * not supported by the queueing system.
	 *
	 * @param maxNumberOfMessages
	 * 		the maximum number of messages (between 1-10)
	 */
	public void setMaxNumberOfMessages(Integer maxNumberOfMessages) {
		this.maxNumberOfMessages = maxNumberOfMessages;
	}

	/**
	 * Configures the duration (in seconds) that the received messages are hidden from
	 * subsequent poll requests after being retrieved from the system.
	 *
	 * @param visibilityTimeout
	 * 		the visibility timeout in seconds
	 */
	public void setVisibilityTimeout(Integer visibilityTimeout) {
		this.visibilityTimeout = visibilityTimeout;
	}

	/**
	 * Configures the wait timeout that the poll request will wait for new message to arrive if the are currently no
	 * messages on the queue. Higher values will reduce poll request to the system significantly. The value should
	 * be between 1 and 20. For more information read the <a href="http://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-long-polling.html">documentation</a>.
	 *
	 * @param waitTimeOut
	 * 		- the wait time out in seconds
	 */
	public void setWaitTimeOut(Integer waitTimeOut) {
		this.waitTimeOut = waitTimeOut;
	}

	/**
	 * Configures if this container should be automatically started. The default value is true.
	 *
	 * @param autoStartup
	 * 		- false if the container will be manually started
	 */
	public void setAutoStartup(boolean autoStartup) {
		this.autoStartup = autoStartup;
	}

	/**
	 * Sets the {@link AmazonSQSAsync} that is going to be used by the container to interact
	 * with the messaging (SQS) API.
	 *
	 * @param amazonSqs
	 * 		The {@link AmazonSQSAsync}, must not be {@code null}.
	 */
	public void setAmazonSqs(AmazonSQSAsync amazonSqs) {
		Assert.notNull(amazonSqs, "amazonSqs must not be null");
		this.amazonSqs = amazonSqs;
	}

	public AmazonSQS getAmazonSqs() {
		return this.amazonSqs;
	}

	/**
	 * Configures the {@link QueueMessageHandler} that must be used
	 * to handle incoming messages.
	 * <p><b>NOTE</b>: It is rather unlikely that the {@link QueueMessageHandler}
	 * must be configured with this setter. Consider using the
	 * {@link QueueMessageHandlerFactory} to configure the
	 * {@link QueueMessageHandler} before using this setter.</p>
	 *
	 * @param messageHandler
	 * 		the {@link QueueMessageHandler} that must be used by the
	 * 		container, must not be {@code null}.
	 * @see QueueMessageHandlerFactory
	 */
	public void setQueueMessageHandler(QueueMessageHandler messageHandler) {
		Assert.notNull(messageHandler, "messageHandler must not be null");
		this.queueMessageHandler = messageHandler;
	}

	public QueueMessageHandler getQueueMessageHandler() {
		return this.queueMessageHandler;
	}

	/**
	 * This value must be set if no destination resolver has been set.
	 *
	 * @param resourceIdResolver
	 * 		the resourceIdResolver to use for resolving logical to physical ids in a CloudFormation environment.
	 * 		Must not be null.
	 */
	public void setResourceIdResolver(ResourceIdResolver resourceIdResolver) {
		this.resourceIdResolver = resourceIdResolver;
	}

	public ResourceIdResolver getResourceIdResolver() {
		return this.resourceIdResolver;
	}

	/**
	 * Configures the destination resolver used to retrieve the queue url based on the destination name configured for
	 * this instance. <br/>
	 * This setter can be used when a custom configured {@link DestinationResolver}
	 * must be provided. (For example if one want to have the {@link org.springframework.cloud.aws.messaging.support.destination.DynamicQueueUrlDestinationResolver}
	 * with the auto creation of queues set to {@code true}.
	 *
	 * @param destinationResolver
	 * 		another or customized {@link DestinationResolver}
	 */
	public void setDestinationResolver(DestinationResolver<String> destinationResolver) {
		this.destinationResolver = destinationResolver;
	}

	/**
	 * Defines if a message must be deleted or not if the handler method throws an exception and the exception handler
	 * method is called. By default this value is set to {@code true} which means that the message is deleted to avoid
	 * poison messages. If this value is set to {@code false} it is the responsibility of the exception handler method to delete
	 * the message. The exception handler method can inject the message headers with {@link org.springframework.messaging.handler.annotation.Headers}
	 * in order to get the receipt handle.
	 *
	 * @param deleteMessageOnException
	 * 		whether a message must be deleted or not when the handler method throws an exception
	 */
	public void setDeleteMessageOnException(Boolean deleteMessageOnException) {
		this.deleteMessageOnException = deleteMessageOnException;
	}

	/**
	 * @return The number of milliseconds the polling thread must wait before trying to recover when an error occurs
	 * (e.g. connection timeout)
	 */
	public Long getBackOffTime() {
		return this.backOffTime;
	}

	/**
	 * The number of milliseconds the polling thread must wait before trying to recover when an error occurs
	 * (e.g. connection timeout). Default value is 10000 milliseconds.
	 *
	 * @param backOffTime
	 * 		in milliseconds
	 */
	public void setBackOffTime(Long backOffTime) {
		this.backOffTime = backOffTime;
	}

	public SimpleMessageListenerContainer createSimpleMessageListenerContainer() {
		Assert.notNull(this.amazonSqs, "amazonSqs must not be null");

		SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
		simpleMessageListenerContainer.setAmazonSqs(this.amazonSqs);
		simpleMessageListenerContainer.setAutoStartup(this.autoStartup);

		if (this.taskExecutor != null) {
			simpleMessageListenerContainer.setTaskExecutor(this.taskExecutor);
		}
		if (this.maxNumberOfMessages != null) {
			simpleMessageListenerContainer.setMaxNumberOfMessages(this.maxNumberOfMessages);
		}
		if (this.visibilityTimeout != null) {
			simpleMessageListenerContainer.setVisibilityTimeout(this.visibilityTimeout);
		}
		if (this.waitTimeOut != null) {
			simpleMessageListenerContainer.setWaitTimeOut(this.waitTimeOut);
		}
		if (this.resourceIdResolver != null) {
			simpleMessageListenerContainer.setResourceIdResolver(this.resourceIdResolver);
		}
		if (this.destinationResolver != null) {
			simpleMessageListenerContainer.setDestinationResolver(this.destinationResolver);
		}
		if (this.deleteMessageOnException != null) {
			simpleMessageListenerContainer.setDeleteMessageOnException(this.deleteMessageOnException);
		}
		if (this.backOffTime != null) {
			simpleMessageListenerContainer.setBackOffTime(this.backOffTime);
		}

		return simpleMessageListenerContainer;
	}
}
